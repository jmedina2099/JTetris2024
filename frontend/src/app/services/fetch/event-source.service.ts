import { Injectable } from '@angular/core';
import { Observable, Subscriber } from 'rxjs';
import { Box } from 'src/app/model/figure/box';

@Injectable({
  providedIn: 'root',
})
export class EventSourceService {
  observeMessages(url: string): Observable<Box> {
    return new Observable<Box>((observer: Subscriber<Box>) => {
      const eventSource = new EventSource(url);
      eventSource.addEventListener(
        'message',
        (messageEvent: MessageEvent<string>) => {
          if (messageEvent.data != undefined) {
            observer.next(JSON.parse(messageEvent.data) as Box);
          }
        }
      );
      eventSource.addEventListener('error', () => {
        observer.complete();
      });
      return () => eventSource.close();
    });
  }
}
