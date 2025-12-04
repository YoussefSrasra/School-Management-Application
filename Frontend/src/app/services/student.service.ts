import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Page, Student, Level } from '../models/types';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  private apiUrl = 'http://localhost:8080/api/students';

  constructor(private http: HttpClient) { }

  getAllStudents(page: number = 0, size: number = 10): Observable<Page<Student>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get<Page<Student>>(this.apiUrl, { params });
  }

  getStudentById(id: number): Observable<Student> {
    return this.http.get<Student>(`${this.apiUrl}/${id}`);
  }

  createStudent(student: Student): Observable<Student> {
    return this.http.post<Student>(this.apiUrl, student);
  }

  updateStudent(id: number, student: Student): Observable<Student> {
    return this.http.put<Student>(`${this.apiUrl}/${id}`, student);
  }

  deleteStudent(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  searchStudents(username: string, page: number = 0, size: number = 10): Observable<Page<Student>> {
    const params = new HttpParams()
      .set('username', username)
      .set('page', page)
      .set('size', size);
    return this.http.get<Page<Student>>(`${this.apiUrl}/search`, { params });
  }

  filterByLevel(level: Level, page: number = 0, size: number = 10): Observable<Page<Student>> {
    const params = new HttpParams()
      .set('level', level)
      .set('page', page)
      .set('size', size);
    return this.http.get<Page<Student>>(`${this.apiUrl}/filter`, { params });
  }
}
