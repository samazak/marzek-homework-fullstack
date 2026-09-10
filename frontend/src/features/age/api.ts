import { apiClient, type ApiClient } from '../../api/client'

export interface AgeResult { age: string; text: string }

export function age(value: string, client: ApiClient = apiClient): Promise<AgeResult> {
  return client.request(`/age?age=${encodeURIComponent(value)}`)
}
