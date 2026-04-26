// ChatBot.js
// The smart chat interface — talks to the chat service via WebSocket
// For now it's a simulated conversation using fake responses

import React, { useState, useRef, useEffect } from 'react';

// Simulates bot responses — replace with real WebSocket later
function getBotReply(userMessage, step) {
  if (step === 0) {
    return {
      text: `Searching for incidents similar to "${userMessage}"...`,
      showSuggestions: true,
    };
  }
  if (step === 1) {
    return { text: 'Did any of those solutions help you?' };
  }
  return { text: 'I created an incident for you. A technician will contact you within 2 hours.' };
}

export default function ChatBot() {
  const [messages, setMessages] = useState([
    {
      role: 'bot',
      text: 'Hello! Describe your problem and I will search for similar resolved incidents.',
      suggestions: null,
    },
  ]);
  const [input, setInput] = useState('');
  const [step, setStep] = useState(0);
  const bottomRef = useRef(null);

  // Auto-scroll to the bottom when a new message arrives
  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  function sendMessage() {
    if (!input.trim()) return;

    const userMsg = { role: 'user', text: input };
    const reply = getBotReply(input, step);

    const botMsg = {
      role: 'bot',
      text: reply.text,
      suggestions: reply.showSuggestions
        ? [
            { id: 'INC-245', title: 'HP printer blocked', solution: 'Restart the print spooler service', votes: 8 },
            { id: 'INC-189', title: 'Printer not responding', solution: 'Reinstall the printer drivers', votes: 5 },
          ]
        : null,
    };

    setMessages((prev) => [...prev, userMsg, botMsg]);
    setInput('');
    setStep((s) => s + 1);

    // TODO: Replace with real WebSocket
    // socket.send(JSON.stringify({ type: 'USER_MESSAGE', text: input }));
  }

  function handleSolved(incidentId) {
    const msg = {
      role: 'bot',
      text: `Great! Marking this as resolved. The solution from ${incidentId} helped you.`,
    };
    setMessages((prev) => [...prev, msg]);
  }

  function handleNotSolved() {
    const msg = {
      role: 'bot',
      text: 'No problem! Creating a new incident for you now... Incident #INC-999 created. A technician will contact you within 2 hours.',
    };
    setMessages((prev) => [...prev, msg]);
  }

  return (
    <div>
      <div className="page-header">
        <h1>Chat Support</h1>
      </div>

      <div className="chat-wrap">
        <div className="chat-messages">
          {messages.map((msg, i) => (
            <div key={i}>
              <div className={`msg ${msg.role}`}>{msg.text}</div>

              {/* Show suggestion cards under the bot message */}
              {msg.suggestions && (
                <div style={{ display: 'flex', flexDirection: 'column', gap: 8, marginTop: 8, maxWidth: '70%' }}>
                  {msg.suggestions.map((s) => (
                    <div
                      key={s.id}
                      className="card"
                      style={{ padding: '12px 16px' }}
                    >
                      <div style={{ fontWeight: 600, fontSize: 13, color: 'var(--accent)' }}>
                        {s.id}
                      </div>
                      <div style={{ fontSize: 13, marginTop: 2 }}>{s.title}</div>
                      <div style={{ fontSize: 12, color: 'var(--muted)', marginTop: 4 }}>
                        Solution: {s.solution}
                      </div>
                      <div style={{ fontSize: 11, color: 'var(--ok)', marginTop: 2 }}>
                        Helped {s.votes} users
                      </div>
                      <div style={{ display: 'flex', gap: 8, marginTop: 10 }}>
                        <button
                          className="btn sm"
                          style={{ background: '#DCFCE7', color: '#14532D', border: '1px solid #16A34A' }}
                          onClick={() => handleSolved(s.id)}
                        >
                          ✓ This worked!
                        </button>
                        <button className="btn sm" onClick={handleNotSolved}>
                          ✗ Didn't work
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          ))}
          <div ref={bottomRef} />
        </div>

        <div className="chat-input">
          <input
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === 'Enter' && sendMessage()}
            placeholder="Describe your problem..."
          />
          <button className="btn primary" onClick={sendMessage}>Send</button>
        </div>
      </div>
    </div>
  );
}
