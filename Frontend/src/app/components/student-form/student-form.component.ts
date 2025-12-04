import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { StudentService } from '../../services/student.service';
import { Student, Level } from '../../models/types';

@Component({
  selector: 'app-student-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './student-form.component.html'
})
export class StudentFormComponent {

  @Input() student: Student = { username: '', level: Level.SEPTIEME }; // Default for Create mode
  @Input() isEditMode: boolean = false;

  @Output() close = new EventEmitter<void>(); // To close the modal
  @Output() refreshList = new EventEmitter<void>(); // To tell parent to reload data

  levels = Object.values(Level);
  errorMessage: string = '';
  isLoading: boolean = false;

  constructor(private studentService: StudentService) {}

  save(): void {
    this.isLoading = true;
    this.errorMessage = '';

    if (this.isEditMode && this.student.id) {
      // UPDATE Mode
      this.studentService.updateStudent(this.student.id, this.student).subscribe({
        next: () => {
          this.isLoading = false;
          this.refreshList.emit();
          this.close.emit();
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = 'Error updating student. Username might already exist.';
        }
      });
    } else {
      // CREATE Mode
      this.studentService.createStudent(this.student).subscribe({
        next: () => {
          this.isLoading = false;
          this.refreshList.emit();
          this.close.emit();
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = 'Error creating student. Username might already exist.';
        }
      });
    }
  }

  cancel(): void {
    this.close.emit();
  }
}
