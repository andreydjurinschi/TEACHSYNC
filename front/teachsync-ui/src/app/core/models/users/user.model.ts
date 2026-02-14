
/* user model dto example for users list*/ 
export interface User {
    id: number;
    fullName: string;
    email: string;
    registeredAt: string;
    role: 'ADMIN' | 'MANAGER' | 'TEACHER';
}