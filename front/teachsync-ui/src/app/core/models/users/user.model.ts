
/* user model dto example for users list*/ 
export interface User {
    id: number;
    name: string;
    surname: string;
    email: string;
    registeredAt: string;
    role: 'ADMIN' | 'MANAGER' | 'TEACHER';
}