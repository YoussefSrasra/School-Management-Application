export interface AuthRequest {
  username?: string;
  password?: string;
}

export interface AuthResponse {
  token: string;
}

export enum Level {
  SEPTIEME = 'SEPTIEME',
  HUITIEME = 'HUITIEME',
  NEUVIEME = 'NEUVIEME'
}

export interface Student {
  id?: number;
  username: string;
  level: Level;
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // current page
}
