// IncidentDetail.js
// Shows one incident in detail + comments section

import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { StatusBadge, PriorityBadge } from '../components/Badge';
import { MOCK_INCIDENTS, MOCK_COMMENTS } from '../mockData';
// TODO: import api from '../api';

export default function IncidentDetail() {
  const { id } = useParams(); // gets the ID from the URL e.g. /incident/INC-001
  const navigate = useNavigate();

  const [incident, setIncident] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');

  useEffect(() => {
    // --- MOCK VERSION ---
    const found = MOCK_INCIDENTS.find((i) => i.id === id);
    setIncident(found || null);
    setComments(MOCK_COMMENTS.filter((c) => c.incidentId === id));

    // --- REAL VERSION ---
    // api.get(`/api/incidents/${id}`).then(res => setIncident(res.data));
    // api.get(`/api/comments?incidentId=${id}`).then(res => setComments(res.data));
  }, [id]);

  function addComment() {
    if (!newComment.trim()) return;

    const comment = {
      id: Date.now(),
      incidentId: id,
      author: 'Ali Hassan (You)',
      text: newComment,
      createdAt: new Date().toLocaleString(),
    };

    setComments([...comments, comment]);
    setNewComment('');

    // --- REAL VERSION ---
    // api.post('/api/comments', { incidentId: id, text: newComment });
  }

  if (!incident) return <div className="empty">Incident not found.</div>;

  return (
    <div>
      <div className="page-header">
        <h1>
          <span className="tag-id">{incident.id}</span>{' '}
          {incident.title}
        </h1>
        <button className="btn" onClick={() => navigate('/')}>← Back</button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 280px', gap: 20 }}>

        {/* Left: description + comments */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>

          <div className="card">
            <h3 style={{ marginBottom: 10, fontSize: 14, fontWeight: 600 }}>Description</h3>
            <p style={{ color: 'var(--muted)', lineHeight: 1.7 }}>{incident.description}</p>
          </div>

          <div className="card">
            <h3 style={{ marginBottom: 14, fontSize: 14, fontWeight: 600 }}>
              Comments ({comments.length})
            </h3>

            {comments.length === 0 && (
              <div style={{ color: 'var(--muted)', fontSize: 13 }}>No comments yet.</div>
            )}

            {comments.map((c) => (
              <div key={c.id} style={{ borderBottom: '1px solid var(--border)', paddingBottom: 12, marginBottom: 12 }}>
                <div style={{ fontWeight: 600, fontSize: 13 }}>{c.author}</div>
                <div style={{ fontSize: 13, color: 'var(--muted)', marginTop: 2 }}>{c.text}</div>
                <div style={{ fontSize: 11, color: 'var(--muted)', marginTop: 4 }}>{c.createdAt}</div>
              </div>
            ))}

            {/* Add comment */}
            <div style={{ display: 'flex', gap: 8, marginTop: 8 }}>
              <input
                value={newComment}
                onChange={(e) => setNewComment(e.target.value)}
                placeholder="Add a comment..."
                onKeyDown={(e) => e.key === 'Enter' && addComment()}
              />
              <button className="btn primary" onClick={addComment}>Post</button>
            </div>
          </div>

        </div>

        {/* Right: incident info */}
        <div className="card" style={{ height: 'fit-content' }}>
          <h3 style={{ marginBottom: 14, fontSize: 14, fontWeight: 600 }}>Details</h3>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 10, fontSize: 13 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--muted)' }}>Status</span>
              <StatusBadge status={incident.status} />
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--muted)' }}>Priority</span>
              <PriorityBadge priority={incident.priority} />
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--muted)' }}>Created</span>
              <span>{incident.createdAt}</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--muted)' }}>Assigned to</span>
              <span>{incident.assignedTo || '—'}</span>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
}
