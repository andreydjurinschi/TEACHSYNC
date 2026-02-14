
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs/internal/Observable';
import { User } from '../models/users/user.model';
import { UserWithCourses } from '../models/users/user.detailed.model';

@Injectable({
  providedIn: 'root'
})
export class UserService{
    private apiUrl = 'http://localhost:8080/teachsync/users';

    constructor(private http: HttpClient) {}

    getAll(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/all`);
    }
    getById(id: number): Observable<User> {
      return this.http.get<User>(`${this.apiUrl}/${id}`);
    }

    update(id: number, user: Partial<User>): Observable<User> {
      return this.http.put<User>(`${this.apiUrl}/edit/${id}`, user);
    }
    create(user: Partial<User>){
      return this.http.post<User>(`${this.apiUrl}/create`, user);
    }

    getWithCourses(id:  number)  : Observable<UserWithCourses>{
        return this.http.get<UserWithCourses>(`${this.apiUrl}/teacher/${id}/courses`);
    }
      
}