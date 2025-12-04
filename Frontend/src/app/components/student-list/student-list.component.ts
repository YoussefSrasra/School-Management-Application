import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../services/student.service';
import { Student, Level, Page } from '../../models/types';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './student-list.component.html',
  styleUrls: ['./student-list.component.css']
})
export class StudentListComponent implements OnInit {

  students: Student[] = [];
  levels = Object.values(Level); // Populates the dropdown with SEPTIEME, HUITIEME, etc.

  // Pagination State
  currentPage: number = 0;
  pageSize: number = 5;
  totalPages: number = 0;
  totalElements: number = 0;

  // Filter/Search State
  searchQuery: string = '';
  selectedLevel: string = '';

  constructor(
    private studentService: StudentService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    // If searching, call search API
    if (this.searchQuery.trim()) {
      this.studentService.searchStudents(this.searchQuery, this.currentPage, this.pageSize)
        .subscribe(data => this.handleResponse(data));
    }
    // If filtering by level, call filter API
    else if (this.selectedLevel) {
      this.studentService.filterByLevel(this.selectedLevel as Level, this.currentPage, this.pageSize)
        .subscribe(data => this.handleResponse(data));
    }
    // Otherwise, get all
    else {
      this.studentService.getAllStudents(this.currentPage, this.pageSize)
        .subscribe(data => this.handleResponse(data));
    }
  }

  handleResponse(data: Page<Student>): void {
    this.students = data.content;
    this.totalPages = data.totalPages;
    this.totalElements = data.totalElements;
  }

  onSearch(): void {
    this.currentPage = 0; // Reset to page 1 on new search
    this.selectedLevel = ''; // Clear level filter if searching
    this.loadStudents();
  }

  onFilterChange(): void {
    this.currentPage = 0;
    this.searchQuery = ''; // Clear search if filtering
    this.loadStudents();
  }

  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadStudents();
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
