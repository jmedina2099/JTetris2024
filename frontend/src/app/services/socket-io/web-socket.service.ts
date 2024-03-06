import { Injectable } from '@angular/core';
import { Socket, io } from 'socket.io-client';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  private socket: Socket | undefined = undefined;

  constructor() {
    if (!this.socket) {
      this.socket = io('http://localhost:4444/');
    }
  }

  public getSocket(): Socket | undefined {
    return this.socket;
  }
}
