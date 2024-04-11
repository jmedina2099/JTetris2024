import { Injectable } from '@angular/core';
import { Socket, io } from 'socket.io-client';

@Injectable({
  providedIn: 'root',
})
export class WebSocketService {
  private socket: Socket | undefined = undefined;

  public getSocket(): Socket {
    return this.createSocket();
  }

  private createSocket(): Socket {
    if (this.socket === undefined) {
      this.socket = io('http://localhost:4444/');
    }
    return this.socket;
  }
}
