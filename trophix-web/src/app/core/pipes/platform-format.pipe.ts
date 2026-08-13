import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'platformFormat',
  standalone: true,
})
export class PlatformFormatPipe implements PipeTransform {
  transform(value: string | null | undefined): string {
    if (!value) return '';
    return value.replace(/PSPC/g, 'PC').split(',').join(' / ');
  }
}
