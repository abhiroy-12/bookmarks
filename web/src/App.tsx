import { useCallback, useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import {
  ApiError,
  createBookmark,
  deleteBookmark,
  listBookmarks,
  type Bookmark,
} from './api'
import './App.css'

function errorMessage(err: unknown): string {
  if (err instanceof ApiError || err instanceof Error) return err.message
  return 'Something went wrong'
}

function App() {
  const [bookmarks, setBookmarks] = useState<Bookmark[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const [url, setUrl] = useState('')
  const [title, setTitle] = useState('')
  const [description, setDescription] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const [formError, setFormError] = useState<string | null>(null)

  const [deletingId, setDeletingId] = useState<string | null>(null)

  const load = useCallback(async (signal?: AbortSignal) => {
    setLoading(true)
    setError(null)
    try {
      const data = await listBookmarks(signal)
      setBookmarks(data)
    } catch (err) {
      if (signal?.aborted) return
      setError(errorMessage(err))
    } finally {
      if (!signal?.aborted) setLoading(false)
    }
  }, [])

  useEffect(() => {
    const controller = new AbortController()
    void load(controller.signal)
    return () => controller.abort()
  }, [load])

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setSubmitting(true)
    setFormError(null)
    try {
      const created = await createBookmark({
        url: url.trim(),
        title: title.trim(),
        description: description.trim() || null,
      })
      setBookmarks((prev) => [...prev, created])
      setUrl('')
      setTitle('')
      setDescription('')
    } catch (err) {
      setFormError(errorMessage(err))
    } finally {
      setSubmitting(false)
    }
  }

  async function handleDelete(id: string) {
    setDeletingId(id)
    setError(null)
    try {
      await deleteBookmark(id)
      setBookmarks((prev) => prev.filter((bookmark) => bookmark.id !== id))
    } catch (err) {
      setError(errorMessage(err))
    } finally {
      setDeletingId(null)
    }
  }

  return (
    <main className="app">
      <h1>Bookmarks</h1>

      <form className="bookmark-form" onSubmit={handleSubmit}>
        <input
          type="url"
          placeholder="https://example.com"
          value={url}
          onChange={(event) => setUrl(event.target.value)}
          required
        />
        <input
          type="text"
          placeholder="Title"
          value={title}
          onChange={(event) => setTitle(event.target.value)}
          required
        />
        <input
          type="text"
          placeholder="Description (optional)"
          value={description}
          onChange={(event) => setDescription(event.target.value)}
        />
        <button type="submit" disabled={submitting}>
          {submitting ? 'Adding…' : 'Add bookmark'}
        </button>
        {formError && (
          <p className="error" role="alert">
            {formError}
          </p>
        )}
      </form>

      {loading ? (
        <p>Loading…</p>
      ) : error ? (
        <div className="error" role="alert">
          <p>{error}</p>
          <button type="button" onClick={() => void load()}>
            Retry
          </button>
        </div>
      ) : bookmarks.length === 0 ? (
        <p>No bookmarks yet.</p>
      ) : (
        <ul className="bookmark-list">
          {bookmarks.map((bookmark) => (
            <li key={bookmark.id}>
              <a href={bookmark.url} target="_blank" rel="noreferrer">
                {bookmark.title}
              </a>
              {bookmark.description && <p>{bookmark.description}</p>}
              <button
                type="button"
                disabled={deletingId === bookmark.id}
                onClick={() => void handleDelete(bookmark.id)}
              >
                {deletingId === bookmark.id ? 'Deleting…' : 'Delete'}
              </button>
            </li>
          ))}
        </ul>
      )}
    </main>
  )
}

export default App
