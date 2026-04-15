import { CommonModule, isPlatformBrowser } from "@angular/common";
import { Component, inject, OnInit, PLATFORM_ID, signal } from "@angular/core";
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from "@angular/forms";
import { ActivatedRoute, Router, RouterLink } from "@angular/router";
import { GroupService } from "../../../core/services/group.service";
import { GroupBase } from "../../../core/models/groups/group.model";

@Component({
    selector: 'app-group-edit',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterLink],
    templateUrl: './group-edit.html'
})
export class GroupEdit implements OnInit{

    form!: FormGroup;
    group = signal<GroupBase | null> (null)
    loading = signal(false)
    
    private platformId = inject(PLATFORM_ID)
    private route = inject(ActivatedRoute)
    private router = inject(Router)
    private formBuilder = inject(FormBuilder)
    private groupService = inject(GroupService)

    ngOnInit(): void {
        this.form = this.formBuilder.group({
            name: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(10)]],
            capacity: [null, [Validators.required, Validators.minLength(12), Validators.maxLength(35)]],
            openDate: [''],
        })

        if(isPlatformBrowser(this.platformId)){
            const id = Number(this.route.snapshot.paramMap.get("id"))
            
            this.groupService.getById(id).subscribe({
                next: data => {
                    this.group.set(data)
                    this.form.patchValue({
                        name: data.name,
                        capacity: data.capacity,
                        openDate: data.date
                    })
                    console.log(data)
                }
            })
        }
    }
    
    submit(): void {
        if(this.form.invalid) return
        const id = Number(this.route.snapshot.paramMap.get("id"))
        this.loading.set(true)
        this.groupService.update(id, this.form.value).subscribe({
            next: () => this.router.navigate(["/groups", id]),
            error: err => {
                console.error(err)
                this.loading.set(false)
            }
        })
    }
}
