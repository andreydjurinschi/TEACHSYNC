import { HttpClient, HttpHeaders, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { ProfileInfo } from "../models/profile/profile-info.model";

@Injectable({providedIn: 'root'})
export class ProfileService {
    
    private apiURL = 'http://localhost:8090/teachsync/account/info'

    constructor(private http: HttpClient){}

    private getHeaders(): HttpHeaders{
        const token = localStorage.getItem('jwt_token')
        return new HttpHeaders({
            Authorization: `Bearer ${token}`
        })
    }

    getProfileInfo(email: string): Observable<ProfileInfo>{
        const param = new HttpParams().set('email', email)
        return this.http.get<ProfileInfo>(`${this.apiURL}`, {params: param, headers: this.getHeaders()})
    }
}