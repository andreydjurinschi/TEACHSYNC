import { DOCUMENT, isPlatformBrowser } from '@angular/common';
import { Inject, Injectable, PLATFORM_ID, signal } from '@angular/core';

type AppTheme = 'dark' | 'light';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  theme = signal<AppTheme>('light');

  constructor(
    @Inject(DOCUMENT) private document: Document,
    @Inject(PLATFORM_ID) private platformId: object,
  ) {
    if (!isPlatformBrowser(this.platformId)) return;

    const savedTheme = localStorage.getItem('theme') as AppTheme | null;
    const initialTheme: AppTheme = savedTheme === 'dark' || savedTheme === 'light' ? savedTheme : 'light';
    this.setTheme(initialTheme);
  }

  toggle() {
    this.setTheme(this.theme() === 'dark' ? 'light' : 'dark');
  }

  private setTheme(next: AppTheme): void {
    this.theme.set(next);

    if (!isPlatformBrowser(this.platformId)) return;

    localStorage.setItem('theme', next);
    this.document.documentElement.classList.toggle('dark', next === 'dark');
  }
}
