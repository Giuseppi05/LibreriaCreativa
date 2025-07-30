import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth-service';
import { map, take } from 'rxjs/operators';
import { inject } from '@angular/core';

export const adminGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.isAdmin$.pipe(
    take(1),
    map(isAdmin => isAdmin ? true : router.createUrlTree(['/home']))
  );
};