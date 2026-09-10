import { apiClient, ApiError, type ApiClient } from '../../api/client'

export async function session(client: ApiClient = apiClient): Promise<boolean> {
  const response = await client.requestRaw('/session')
  return response.ok
}

export async function login(username: string, password: string, client: ApiClient = apiClient): Promise<void> {
  const response = await client.requestRaw('/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded', ...await client.csrfHeader() },
    body: new URLSearchParams({ username, password }),
  })
  if (!response.ok) throw new ApiError('Invalid username or password.', response.status)
  client.resetCsrfCache()
}

export async function logout(client: ApiClient = apiClient): Promise<void> {
  try {
    const response = await client.requestRaw('/logout', { method: 'POST', headers: await client.csrfHeader() })
    if (!response.ok) throw new ApiError('Sign out failed. Please try again.', response.status)
  } finally {
    client.resetCsrfCache()
  }
}
