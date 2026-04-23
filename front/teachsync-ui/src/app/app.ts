import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ThemeService } from './core/services/theme.service';
import { RuleService } from './core/services/role.rule.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.html',
})
export class App {
  private themeService = inject(ThemeService);

  constructor(public ruleService: RuleService) {
    document.documentElement.classList.toggle('dark', this.themeService.theme() === 'dark');
  }
}
