import { Injectable } from '@angular/core';
import { Client, IMessage } from '@stomp/stompjs';
import { Observable, Subject } from 'rxjs';
import { WS_BASE } from '../config/api.config';
import { PortingRequestResponse } from '../models/porting-request.model';

const DESTINATION = '/topic/porting-requests';

/**
 * One WebSocket connection for the whole app's lifetime (root-provided
 * singleton — connects lazily on first injection, not per-page). Pages
 * subscribe to `changes$`; they don't own the connection.
 *
 * This is the "push" half of the backend's Observer pattern: PortingRequestNotifier
 * publishes to /topic/porting-requests whenever a request changes, and every
 * connected client — this one included — gets it instantly, no polling required.
 */
@Injectable({ providedIn: 'root' })
export class RealtimeService {
  private readonly client: Client;
  private readonly changes = new Subject<PortingRequestResponse>();

  readonly changes$: Observable<PortingRequestResponse> = this.changes.asObservable();

  constructor() {
    this.client = new Client({
      brokerURL: WS_BASE,
      reconnectDelay: 3000,
      onConnect: () => {
        this.client.subscribe(DESTINATION, (message: IMessage) => {
          this.changes.next(JSON.parse(message.body) as PortingRequestResponse);
        });
      },
      // Non-fatal by design: if the socket never connects (e.g. a firewall
      // blocking WS upgrades), the page's existing poll is the fallback.
      onStompError: (frame) => console.error('STOMP error:', frame.headers['message']),
      onWebSocketError: (event) => console.warn('WebSocket error (falling back to polling):', event),
    });
    this.client.activate();
  }
}
