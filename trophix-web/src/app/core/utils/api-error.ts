import { Injectable } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';

/**
 * Resolves the user-facing error message (PT-BR) from an API error,
 * falling back to a generic message when unavailable.
 */
export function apiErrorMessage(error: unknown, fallback: string): string {
  if (error instanceof HttpErrorResponse && error.error?.message) {
    return error.error.message as string;
  }
  return fallback;
}
