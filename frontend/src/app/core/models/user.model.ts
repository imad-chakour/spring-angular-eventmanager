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
  firstName?: string;
  lastName?: string;
  password?: string;
  role: UserRole;
  status?: UserStatus;
  // Marketing fields (previously in Participant)
  phone?: string;
  company?: string;
  jobTitle?: string;
  segments?: string[];
  communicationPreferences?: string[];
  optInMarketing?: boolean;
  lastActivity?: string;
  lastLogin?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  email: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName?: string;
  lastName?: string;
  role?: UserRole;
}
