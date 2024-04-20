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
    const auth = sessionStorage.getItem('authorization');
    if (auth !== null) {
      const xhr = req.clone({
        headers: req.headers.set('authorization', auth),
      });
      return next.handle(xhr);
    }
    return next.handle(req);
  }
}
