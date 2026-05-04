import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <section class="flex min-h-screen items-center justify-center bg-slate-100 px-6 py-12 dark:bg-slate-950">
      <div class="w-full max-w-xl rounded-2xl border border-slate-200 bg-white p-8 text-center shadow-sm dark:border-slate-800 dark:bg-slate-900">
        <p class="text-sm font-semibold uppercase tracking-[0.3em] text-blue-500">404</p>
        <h1 class="mt-4 text-3xl font-semibold text-slate-900 dark:text-white">Page not found</h1>
        <p class="mt-3 text-sm leading-6 text-slate-500 dark:text-slate-400">
          Такой страницы не существует или адрес введен неверно.
        </p>
        <div class="mt-8 flex flex-col gap-3 sm:flex-row sm:justify-center">
          <a routerLink="/profile" class="app-btn-primary">Вернуться в приложение</a>
          <a routerLink="/login" class="app-btn-secondary">На вход</a>
        </div>
      </div>
    </section>
  `,
})
export class NotFoundPageComponent {}
