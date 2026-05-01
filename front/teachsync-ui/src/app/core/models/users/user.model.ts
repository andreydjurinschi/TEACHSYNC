
export interface User {
    id: number;
    name: string;
    surname: string;
    email: string;
    profilePicture?: string;
    registeredAt: string;
    role: 'ADMIN' | 'MANAGER' | 'TEACHER';
}
