export interface AuthResponse {
  accessToken: string;
  tokenType: string;
  email: string;
  fullName: string;
  role: string;
  expiresIn: number;
}
