import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AppService } from '../services/app/app.service';

@Injectable({
  providedIn: 'root',
})
export class XhrInterceptor implements HttpInterceptor {
  constructor(private app: AppService) {}

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    if (
      this.app.credentials.username !== '' &&
      this.app.credentials.password !== ''
    ) {
      const xhr = req.clone({
        headers: req.headers.set(
          'Authorization',
          'Basic ' +
            btoa(
              this.app.credentials.username +
                ':' +
                this.app.credentials.password
            )
        ),
      });
      return next.handle(xhr);
    }
    return next.handle(req);
  }
}
