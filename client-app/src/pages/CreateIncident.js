// CreateIncident.js
// Form to create a new incident

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
// TODO: import api from '../api';

export default function CreateIncident() {
  const navigate = useNavigate();

  // Form state — one variable per field
  const [form, setForm] = useState({
    title: '',
    description: '',
    priority: 'medium',
    department: 'IT',
  });

  const [screenshot, setScreenshot] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Update the field that changed
  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError('');

    if (!form.title || !form.description) {
      setError('Please fill in all required fields.');
      return;
    }

    setLoading(true);

    // --- MOCK VERSION ---
    setTimeout(() => {
      alert('Incident created! (mock)');
      navigate('/');
    }, 800);

    // --- REAL VERSION (uncomment when backend is ready) ---
    // try {
    //   const formData = new FormData();
    //   formData.append('title', form.title);
    //   formData.append('description', form.description);
    //   formData.append('priority', form.priority);
    //   formData.append('department', form.department);
    //   if (screenshot) formData.append('screenshot', screenshot);
    //   await api.post('/api/incidents', formData);
    //   navigate('/');
    // } catch (err) {
    //   setError('Failed to create incident. Try again.');
    // } finally {
    //   setLoading(false);
    // }
  }

  return (
    <div>
      <div className="page-header">
        <h1>New Incident</h1>
        <button className="btn" onClick={() => navigate('/')}>← Back</button>
      </div>

      <div className="card" style={{ maxWidth: 600 }}>
        <form className="form-grid" onSubmit={handleSubmit}>

          {/* Title */}
          <div className="field">
            <label>Title *</label>
            <input
              name="title"
              value={form.title}
              onChange={handleChange}
              placeholder="e.g. Printer not responding"
            />
          </div>

          {/* Description */}
          <div className="field">
            <label>Description *</label>
            <textarea
              name="description"
              value={form.description}
              onChange={handleChange}
              placeholder="Describe the problem in detail..."
            />
          </div>

          {/* Priority + Department in 2 columns */}
          <div className="form-row">
            <div className="field">
              <label>Priority</label>
              <select name="priority" value={form.priority} onChange={handleChange}>
                <option value="low">Low</option>
                <option value="medium">Medium</option>
                <option value="high">High</option>
              </select>
            </div>
            <div className="field">
              <label>Department</label>
              <select name="department" value={form.department} onChange={handleChange}>
                <option>IT</option>
                <option>HR</option>
                <option>Finance</option>
                <option>Operations</option>
              </select>
            </div>
          </div>

          {/* Screenshot upload */}
          <div className="field">
            <label>Screenshot (optional)</label>
            <input
              type="file"
              accept="image/*"
              onChange={(e) => setScreenshot(e.target.files[0])}
            />
          </div>

          {/* Error message */}
          {error && (
            <div style={{ color: 'var(--danger)', fontSize: 13 }}>{error}</div>
          )}

          {/* Buttons */}
          <div style={{ display: 'flex', gap: 10, justifyContent: 'flex-end' }}>
            <button type="button" className="btn" onClick={() => navigate('/')}>
              Cancel
            </button>
            <button type="submit" className="btn primary" disabled={loading}>
              {loading ? 'Submitting...' : 'Submit Incident'}
            </button>
          </div>

        </form>
      </div>
    </div>
  );
}
