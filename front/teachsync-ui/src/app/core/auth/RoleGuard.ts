import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivate,
  Router,
  UrlTree,
} from '@angular/router';

import { AuthService } from '../services/auth.service';
import { RuleService } from '../services/role.rule.service';

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {

  constructor(
    private authService: AuthService,
    private ruleService: RuleService,
    private router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): boolean | UrlTree {
    if (!this.authService.isLoggedIn()) {
      return this.router.parseUrl('/login');
    }

    const allowedRoles = route.data['roles'] as string[] | undefined;
    if (!allowedRoles || allowedRoles.length === 0) {
      return true;
    }

    const currentRole = this.ruleService.getRole();
    if (currentRole && allowedRoles.includes(currentRole)) {
      return true;
    }

    return this.router.parseUrl('/forbidden');
  }
}
