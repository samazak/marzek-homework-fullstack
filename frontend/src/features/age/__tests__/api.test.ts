import { describe, expect, it, vi } from 'vitest'
import { ApiClient } from '../../../api/client'
import { age } from '../api'

describe('age', () => {
  it('encodes the age query parameter', async () => {
    const transport = vi.fn<typeof fetch>()
      .mockResolvedValue(new Response(JSON.stringify({ age: '18', text: 'ok' })))

    await age('18 years', new ApiClient(transport))

    expect(transport.mock.calls[0][0]).toBe('/api/age?age=18%20years')
  })
})
