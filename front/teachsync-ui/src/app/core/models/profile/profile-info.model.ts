export interface ProfileInfo{
    email: string
    firstName: string
    lastName: string
    profilePicture: string
    registeredAt: string
    role: 'ADMIN' | 'MANAGER' | 'TEACHER';
}