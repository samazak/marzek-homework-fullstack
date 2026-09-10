import { describe, expect, it, vi } from 'vitest'
import { ApiClient } from '../client'

describe('ApiClient', () => {
  it('reports session expiry', async () => {
    const transport = vi.fn<typeof fetch>().mockResolvedValue(new Response('', { status: 401 }))

    const request = new ApiClient(transport).request('/anything')

    await expect(request).rejects.toThrow('Sign in')
  })

  it('reports a changed session', async () => {
    const transport = vi.fn<typeof fetch>().mockResolvedValue(new Response('', { status: 403 }))

    const request = new ApiClient(transport).request('/anything')

    await expect(request).rejects.toThrow('session has changed')
  })

  it('normalizes network errors', async () => {
    const transport = vi.fn<typeof fetch>().mockRejectedValue(new TypeError('network'))

    const request = new ApiClient(transport).request('/anything')

    await expect(request).rejects.toThrow('Unable to reach')
  })

  it('reports a safe server error', async () => {
    const transport = vi.fn<typeof fetch>()
      .mockResolvedValue(new Response(JSON.stringify({ detail: 'Try again.' }), { status: 409 }))

    const request = new ApiClient(transport).request('/anything')

    await expect(request).rejects.toThrow('Try again.')
  })
})
