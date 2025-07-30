import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth-service';
import { map, take } from 'rxjs/operators';
import { inject } from '@angular/core';
import { tap } from 'rxjs/operators';

export const nonAdminGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  return auth.isAdmin$.pipe(
    take(1),
    map((isAdmin) =>
      isAdmin ? router.createUrlTree(['/admin/products']) : true
    )
  );
};
