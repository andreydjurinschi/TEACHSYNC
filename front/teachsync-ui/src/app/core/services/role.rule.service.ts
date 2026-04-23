import { inject, Injectable } from "@angular/core"

import { JwtHelperService } from "@auth0/angular-jwt"


@Injectable({providedIn: 'root'})
export class RuleService{
    
  private jwtHelper = inject(JwtHelperService)
    
getRole(): string | null {
            
    const token = localStorage.getItem('jwt_token')
    if(token && !this.jwtHelper.isTokenExpired(token)){
        const decoded = this.jwtHelper.decodeToken(token)
        return decoded.roles  
    }
    return null
  }

getId(): number | null{
  const token = localStorage.getItem('jwt_token')
    if(token && !this.jwtHelper.isTokenExpired(token)){
        const decoded = this.jwtHelper.decodeToken(token)
        return Number(decoded.userId)  
    }
    return null
}

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN'
  }
  isManager(): boolean {
    return this.getRole() === 'MANAGER'
  }
  isTeacher(): boolean {
    return this.getRole() === 'TEACHER'
  }
}