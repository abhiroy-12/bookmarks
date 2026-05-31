// Typed client for the bookmarks REST API.
// Base URL comes from VITE_API_BASE_URL; falls back to the local API default.

export interface Bookmark {
  id: string
  url: string
  title: string
  description: string | null
  createdAt: string
}

export interface CreateBookmarkInput {
  url: string
  title: string
  description?: string | null
}

// RFC 7807 ProblemDetail as returned by the API's @RestControllerAdvice.
interface ProblemDetail {
  title?: string
  detail?: string
  status?: number
  errors?: Record<string, string>
}

export class ApiError extends Error {
  readonly status: number

  constructor(message: string, status: number) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

const BASE_URL = (import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api').replace(/\/+$/, '')

// Turns a non-2xx Response into a thrown ApiError, surfacing ProblemDetail
// validation messages when present.
async function fail(response: Response): Promise<never> {
  let message = `Request failed (${response.status})`
  try {
    const problem: ProblemDetail = await response.json()
    const fieldErrors = problem.errors
      ? Object.entries(problem.errors)
          .map(([field, msg]) => `${field}: ${msg}`)
          .join(', ')
      : ''
    message = fieldErrors || problem.detail || problem.title || message
  } catch {
    // Body was empty or not JSON; keep the status-based message.
  }
  throw new ApiError(message, response.status)
}

export async function listBookmarks(signal?: AbortSignal): Promise<Bookmark[]> {
  const response = await fetch(`${BASE_URL}/bookmarks`, { signal })
  if (!response.ok) return fail(response)
  return response.json() as Promise<Bookmark[]>
}

export async function createBookmark(input: CreateBookmarkInput): Promise<Bookmark> {
  const response = await fetch(`${BASE_URL}/bookmarks`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(input),
  })
  if (!response.ok) return fail(response)
  return response.json() as Promise<Bookmark>
}

export async function deleteBookmark(id: string): Promise<void> {
  const response = await fetch(`${BASE_URL}/bookmarks/${encodeURIComponent(id)}`, {
    method: 'DELETE',
  })
  if (!response.ok) await fail(response)
}
