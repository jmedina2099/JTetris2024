import { Injectable } from '@angular/core';
import { Observable, Subscriber } from 'rxjs';
import { Box } from 'src/app/model/figure/box';
import { EventSourcePolyfill, MessageEvent } from 'event-source-polyfill';
import { Credentials } from 'src/app/components/login/login.component';

const EventSource = EventSourcePolyfill;

@Injectable({
  providedIn: 'root',
})
export class EventSourceService {

  observeMessages(url: string, credentials: Credentials): Observable<Box> {
    return new Observable<Box>((observer: Subscriber<Box>) => {
      const eventSource = new EventSource(url, {
        withCredentials: true,
        lastEventIdQueryParameterName: 'Last-Event-Id',
        headers: {
            'Authorization': 'Basic ' + btoa(credentials.username + ':' + credentials.password)
        }
    });
      eventSource.addEventListener(
        'message',
        (messageEvent) => {
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
