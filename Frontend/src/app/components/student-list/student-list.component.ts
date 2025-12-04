import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../services/student.service';
import { Student, Level, Page } from '../../models/types';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { StudentFormComponent } from '../student-form/student-form.component';
import { ConfirmationModalComponent } from '../confirmation-modal/confirmation-modal.component'; // Import 1

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [CommonModule, FormsModule, StudentFormComponent, ConfirmationModalComponent], // Import 2
  templateUrl: './student-list.component.html',
  styleUrls: ['./student-list.component.css']
})
export class StudentListComponent implements OnInit {

  students: Student[] = [];
  levels = Object.values(Level);

  currentPage: number = 0;
  pageSize: number = 5;
  totalPages: number = 0;
  totalElements: number = 0;

  searchQuery: string = '';
  selectedLevel: string = '';

  // Form Modal State
  showModal: boolean = false;
  isEditMode: boolean = false;
  selectedStudent: Student = { username: '', level: Level.SEPTIEME };

  // Delete Modal State (NEW)
  showDeleteModal: boolean = false;
  studentIdToDelete: number | null = null;

  constructor(
    private studentService: StudentService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    if (this.searchQuery.trim()) {
      this.studentService.searchStudents(this.searchQuery, this.currentPage, this.pageSize)
        .subscribe(data => this.handleResponse(data));
    } else if (this.selectedLevel) {
      this.studentService.filterByLevel(this.selectedLevel as Level, this.currentPage, this.pageSize)
        .subscribe(data => this.handleResponse(data));
    } else {
      this.studentService.getAllStudents(this.currentPage, this.pageSize)
        .subscribe(data => this.handleResponse(data));
    }
  }

  handleResponse(data: Page<Student>): void {
    this.students = data.content;
    this.totalPages = data.totalPages;
    this.totalElements = data.totalElements;
  }

  // --- Form Modal Logic ---
  openCreateModal(): void {
    this.isEditMode = false;
    this.selectedStudent = { username: '', level: Level.SEPTIEME };
    this.showModal = true;
  }

  openEditModal(student: Student): void {
    this.isEditMode = true;
    this.selectedStudent = { ...student };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  onFormSaved(): void {
    this.closeModal();
    this.loadStudents();
  }

  // --- Delete Modal Logic (UPDATED) ---

  // 1. User clicks trash icon -> Open Modal
  deleteStudent(id: number | undefined): void {
    if (id) {
      this.studentIdToDelete = id;
      this.showDeleteModal = true;
    }
  }

  // 2. User clicks "Confirm" in Modal -> Call API
  confirmDelete(): void {
    if (this.studentIdToDelete) {
      this.studentService.deleteStudent(this.studentIdToDelete).subscribe(() => {
        this.loadStudents();
        this.closeDeleteModal();
      });
    }
  }

  // 3. Close modal and reset ID
  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.studentIdToDelete = null;
  }

  // --- Search & Filter ---
  onSearch(): void {
    this.currentPage = 0;
    this.selectedLevel = '';
    this.loadStudents();
  }

  onFilterChange(): void {
    this.currentPage = 0;
    this.searchQuery = '';
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
