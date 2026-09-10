export class ApiError extends Error {
  constructor(message: string, readonly status: number) { super(message) }
}

/** Shared transport boundary; feature api.ts modules build endpoint calls on top. */
export class ApiClient {
  constructor(private readonly transport: typeof fetch = (...args) => fetch(...args)) {}

  private cachedCsrfHeader: Record<string, string> | null = null

  async requestRaw(path: string, init?: RequestInit): Promise<Response> {
    try {
      return await this.transport(`/api${path}`, {
        ...init, credentials: 'same-origin', signal: AbortSignal.timeout(25000),
      })
    } catch { throw new ApiError('Unable to reach the server. Please try again.', 0) }
  }

  async request<T>(path: string, init?: RequestInit): Promise<T> {
    const response = await this.requestRaw(path, init)
    if (!response.ok) {
      if (response.status === 401) throw new ApiError('Sign in to use the Trade Finance Demo.', 401)
      if (response.status === 403) {
        this.cachedCsrfHeader = null
        throw new ApiError('Your session has changed. Refresh and sign in again.', 403)
      }
      const problem = await response.json().catch(() => ({})) as { detail?: string }
      throw new ApiError(problem.detail ?? 'The request could not be completed.', response.status)
    }
    return response.json() as Promise<T>
  }

  /** Cached until a 403 signals the session (and its token) changed, to avoid a GET /csrf before every mutation. */
  async csrfHeader(): Promise<Record<string, string>> {
    if (!this.cachedCsrfHeader) {
      const csrf = await this.request<{ headerName: string; token: string }>('/csrf')
      this.cachedCsrfHeader = { [csrf.headerName]: csrf.token }
    }
    return this.cachedCsrfHeader
  }

  /** The CSRF token is bound to the HTTP session; call after logout so the next login fetches a fresh one. */
  resetCsrfCache(): void {
    this.cachedCsrfHeader = null
  }
}

export const apiClient = new ApiClient()
