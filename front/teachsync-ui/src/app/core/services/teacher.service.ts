import { HttpClient, HttpHeaders } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AuthService } from "./auth.service";
import { TeacherDto } from "../models/users/teacher.model";

@Injectable({ providedIn: 'root' })
export class TeacherService {
  private api = 'http://localhost:8080/teachsync/users';

  constructor(private http: HttpClient, private auth: AuthService) {}

  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('jwt_token');
    return new HttpHeaders({ Authorization: `Bearer ${token}` });
  }

getAll() {
  return this.http.get<TeacherDto[]>(
    `${this.api}/teachers`,
    { headers: this.getHeaders() }
  );
}

  getSpecializations(teacherId: number) {
    return this.http.get<any[]>(
      `${this.api}/${teacherId}/specializations`, { headers: this.getHeaders() }
      
    );
  }

  addSpecialization(teacherId: number, categoryId: number) {
    return this.http.post<void>(
      `${this.api}/${teacherId}/specializations/${categoryId}`,
      {}, { headers: this.getHeaders() }
    );
  }

  removeSpecialization(teacherId: number, categoryId: number) {
    return this.http.delete<void>(
      `${this.api}/${teacherId}/specializations/${categoryId}`, { headers: this.getHeaders() }
    );
  }
}