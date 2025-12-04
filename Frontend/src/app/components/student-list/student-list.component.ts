import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../services/student.service';
import { Student, Level, Page } from '../../models/types';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { StudentFormComponent } from '../student-form/student-form.component';
import { ConfirmationModalComponent } from '../confirmation-modal/confirmation-modal.component';
import { MessageModalComponent } from '../message-modal/message-modal.component'; // Import 1

@Component({
  selector: 'app-student-list',
  standalone: true,
  imports: [CommonModule, FormsModule, StudentFormComponent, ConfirmationModalComponent, MessageModalComponent], // Import 2
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

  // Modal States
  showModal: boolean = false;
  isEditMode: boolean = false;
  selectedStudent: Student = { username: '', level: Level.SEPTIEME };

  showDeleteModal: boolean = false;
  studentIdToDelete: number | null = null;

  // Message Modal State (NEW)
  showMessageModal: boolean = false;
  messageTitle: string = '';
  messageBody: string = '';
  isMessageError: boolean = false;

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


  downloadCsv(): void {
    this.studentService.exportStudents().subscribe({
      next: (blob) => {
        // Create a temporary link to trigger the download
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = 'students.csv';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => this.showMsg('Export Failed', 'Could not download the CSV file.', true)
    });
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.studentService.importStudents(file).subscribe({
        next: () => {
          this.showMsg('Import Successful', 'Students have been imported successfully!', false);
          this.loadStudents();
          event.target.value = '';
        },
        error: () => {
          this.showMsg('Import Failed', 'Please ensure the file is a valid CSV and usernames are unique.', true);
          event.target.value = '';
        }
      });
    }
  }

  showMsg(title: string, message: string, isError: boolean): void {
    this.messageTitle = title;
    this.messageBody = message;
    this.isMessageError = isError;
    this.showMessageModal = true;
  }

  closeMessageModal(): void {
    this.showMessageModal = false;
  }

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

  deleteStudent(id: number | undefined): void {
    if (id) {
      this.studentIdToDelete = id;
      this.showDeleteModal = true;
    }
  }

  confirmDelete(): void {
    if (this.studentIdToDelete) {
      this.studentService.deleteStudent(this.studentIdToDelete).subscribe(() => {
        this.loadStudents();
        this.closeDeleteModal();
      });
    }
  }

  closeDeleteModal(): void {
    this.showDeleteModal = false;
    this.studentIdToDelete = null;
  }

  // --- Search/Filter/Nav ---
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
