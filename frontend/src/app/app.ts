import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { Icon } from './shared/icon/icon';
import { Notifications } from './shared/notifications/notifications';
import { OperatorSwitch } from './shared/operator-switch/operator-switch';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, RouterLinkActive, OperatorSwitch, Notifications, Icon],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}
