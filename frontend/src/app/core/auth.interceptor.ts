import { HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from './auth.service';
import { Observable } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
): Observable<HttpEvent<unknown>> => {
  const auth = inject(AuthService);
  const token = auth.getToken();

  console.log('=== AuthInterceptor ===');
  console.log('Request URL:', req.url);
  console.log('Request method:', req.method);
  console.log('Token present?', !!token);
  console.log('Token value:', token ? token.substring(0, 20) + '...' : 'null');
  console.log('Request headers:', req.headers.keys());

  if (token) {
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
    console.log('Cloned request with Authorization header');
    console.log('Cloned headers:', cloned.headers.keys());
    return next(cloned).pipe(
      catchError((error) => {
        console.error('=== AuthInterceptor: HTTP Error (with token) ===');
        console.error('URL:', req.url);
        console.error('Status:', error.status);
        console.error('StatusText:', error.statusText);
        console.error('Message:', error.message);
        console.error('Error object:', error);
        console.error('Error name:', error.name);
        if (error.status === 0) {
          console.error('⚠️ STATUS 0: Network error or CORS issue!');
          console.error('This usually means:');
          console.error('1. Service is not running');
          console.error('2. CORS is blocking the request');
          console.error('3. Network connectivity issue');
        }
        return throwError(() => error);
      })
    );
  }

  console.warn('⚠️ No token found - request will be sent without Authorization header');
  return next(req).pipe(
    catchError((error) => {
      console.error('=== AuthInterceptor: HTTP Error (no token) ===');
      console.error('URL:', req.url);
      console.error('Status:', error.status);
      console.error('StatusText:', error.statusText);
      console.error('Message:', error.message);
      if (error.status === 0) {
        console.error('⚠️ STATUS 0: Network error or CORS issue!');
      }
      return throwError(() => error);
    })
  );
};


