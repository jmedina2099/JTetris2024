import { Component, Input, OnInit } from '@angular/core';
import { Socket } from 'socket.io-client';
import { Box } from 'src/app/model/figure/box';
import { Figure } from 'src/app/model/figure/figure';
import { Message } from 'src/app/model/message/message';
import { FetchService } from 'src/app/services/fetch/fetch.service';
import { WebSocketService } from 'src/app/services/socket-io/web-socket.service';

@Component({
  selector: 'app-figure',
  templateUrl: './figure.component.html',
  styleUrls: ['./figure.component.css'],
})
export class FigureComponent {

  @Input()
  boxes: Box[] = [];
}