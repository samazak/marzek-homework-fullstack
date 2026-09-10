import { describe, expect, it, vi } from 'vitest'
import { ApiClient } from '../../../api/client'
import { login, logout, session } from '../api'

describe('login', () => {
  it('logs in with the CSRF token as form-encoded credentials', async () => {
    const transport = vi.fn<typeof fetch>()
      .mockResolvedValueOnce(new Response(JSON.stringify({ headerName: 'X-CSRF-TOKEN', token: 'test-token' })))
      .mockResolvedValueOnce(new Response('', { status: 200 }))

    await login('alice', 'hunter2', new ApiClient(transport))

    expect(transport.mock.calls[1][0]).toBe('/api/login')
    expect(transport.mock.calls[1][1]).toMatchObject({
      method: 'POST', credentials: 'same-origin',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded', 'X-CSRF-TOKEN': 'test-token' },
    })
    expect(String(transport.mock.calls[1][1]?.body)).toBe('username=alice&password=hunter2')
  })

  it('rejects a failed login with a generic message', async () => {
    const transport = vi.fn<typeof fetch>()
      .mockResolvedValueOnce(new Response(JSON.stringify({ headerName: 'X-CSRF-TOKEN', token: 'test-token' })))
      .mockResolvedValueOnce(new Response('', { status: 401 }))

    const request = login('alice', 'wrong', new ApiClient(transport))

    await expect(request).rejects.toThrow('Invalid username or password.')
  })

  it('clears the cached CSRF token after a successful login so it is refetched', async () => {
    const transport = vi.fn<typeof fetch>()
      .mockResolvedValueOnce(new Response(JSON.stringify({ headerName: 'X-CSRF-TOKEN', token: 'stale-token' })))
      .mockResolvedValueOnce(new Response('', { status: 200 }))
      .mockResolvedValueOnce(new Response(JSON.stringify({ headerName: 'X-CSRF-TOKEN', token: 'fresh-token' })))
    const client = new ApiClient(transport)

    await login('alice', 'hunter2', client)
    const header = await client.csrfHeader()

    expect(header).toEqual({ 'X-CSRF-TOKEN': 'fresh-token' })
    expect(transport).toHaveBeenCalledTimes(3)
  })
})

describe('logout', () => {
  it('clears the cached CSRF token after a successful logout', async () => {
    const transport = vi.fn<typeof fetch>()
      .mockResolvedValueOnce(new Response(JSON.stringify({ headerName: 'X-CSRF-TOKEN', token: 'stale-token' })))
      .mockResolvedValueOnce(new Response('', { status: 200 }))
      .mockResolvedValueOnce(new Response(JSON.stringify({ headerName: 'X-CSRF-TOKEN', token: 'fresh-token' })))
    const client = new ApiClient(transport)

    await logout(client)
    const header = await client.csrfHeader()

    expect(header).toEqual({ 'X-CSRF-TOKEN': 'fresh-token' })
  })

  it('rejects when the server reports logout failed', async () => {
    const transport = vi.fn<typeof fetch>()
      .mockResolvedValueOnce(new Response(JSON.stringify({ headerName: 'X-CSRF-TOKEN', token: 'test-token' })))
      .mockResolvedValueOnce(new Response('', { status: 500 }))

    const request = logout(new ApiClient(transport))

    await expect(request).rejects.toThrow('Sign out failed. Please try again.')
  })

  it('still clears the cached CSRF token when logout fails', async () => {
    const transport = vi.fn<typeof fetch>()
      .mockResolvedValueOnce(new Response(JSON.stringify({ headerName: 'X-CSRF-TOKEN', token: 'stale-token' })))
      .mockResolvedValueOnce(new Response('', { status: 500 }))
      .mockResolvedValueOnce(new Response(JSON.stringify({ headerName: 'X-CSRF-TOKEN', token: 'fresh-token' })))
    const client = new ApiClient(transport)

    await logout(client).catch(() => {})
    const header = await client.csrfHeader()

    expect(header).toEqual({ 'X-CSRF-TOKEN': 'fresh-token' })
  })
})

describe('session', () => {
  it('reports whether a session is active', async () => {
    const transport = vi.fn<typeof fetch>().mockResolvedValue(new Response('', { status: 401 }))

    const request = session(new ApiClient(transport))

    await expect(request).resolves.toBe(false)
  })
})
