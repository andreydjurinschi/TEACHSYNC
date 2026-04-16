export interface ProfileInfo{
    id: number
    email: string
    name: string
    surname: string
    profilePicture: string
    registeredAt: string
    role: 'ADMIN' | 'MANAGER' | 'TEACHER';
}