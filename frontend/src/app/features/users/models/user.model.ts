export enum UserRole {
  MARKETING_MANAGER = 'MARKETING_MANAGER',
  MARKETING_USER = 'MARKETING_USER',
  ADMIN = 'ADMIN',
  PARTICIPANT = 'PARTICIPANT'
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED'
}

export interface User {
  id?: number;
  email: string;
  firstName: string;
  lastName: string;
  role: UserRole;
  status: UserStatus;
  lastLogin?: Date;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  email: string;
}
