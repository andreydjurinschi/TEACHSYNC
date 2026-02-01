import { CommonModule } from "@angular/common";
import { Component, OnInit, signal } from "@angular/core";
import { User } from "../../../core/models/users/user.model";
import { UserService } from "../../../core/services/user.service";
import { ActivatedRoute, RouterLink } from "@angular/router";
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner"
import { UserWithCourses } from "../../../core/models/users/user.detailed.model";
import { sign } from "crypto";
import { error } from "console";

@Component({
    selector: 'app-user-detailed',
    standalone: true,
    imports: [CommonModule, RouterLink, MatProgressSpinnerModule],
    templateUrl: './user-detailed.html',
})
export class UserDetailed implements OnInit{
    
    user = signal<User | null>(null);
    loading = signal<boolean>(false);   
    userWithCourses = signal<UserWithCourses | null>(null)
    
    constructor(
        private route: ActivatedRoute,
        private userService: UserService
    ) {}

    ngOnInit(): void {
        const userId   = Number(this.route.snapshot.paramMap.get('id'));
        this.loadUser(userId);
    }

    loadUser(id: number): void {
        this.loading.set(true);
        this.userService.getById(id).subscribe({
            next: user => {
                this.user.set(user)
                if(user.role === 'TEACHER'){
                    this.loading.set(true)
                    this.userService.getWithCourses(user.id).subscribe({
                        next: userWihCourses => {
                            this.userWithCourses.set(userWihCourses)
                        },

                    })
                } else{
                    this.userWithCourses.set(null)
                }
            },
        
            error: err => {
                console.error(err);
                this.user.set(null);
            },
            complete: () => this.loading.set(false)
        })
    }

}