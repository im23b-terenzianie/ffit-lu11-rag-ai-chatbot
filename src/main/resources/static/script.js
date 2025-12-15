document.querySelectorAll(".tab-button").forEach(btn => {
    btn.addEventListener("click", () => {
        document.querySelectorAll(".tab-button").forEach(b => b.classList.remove("active"));
        document.querySelectorAll(".tab-content").forEach(c => c.classList.remove("active"));

        btn.classList.add("active");
        document.getElementById(btn.dataset.tab).classList.add("active");
    });
});

async function sendChat() {
  const input = document.getElementById("chat-question");
  const question = input.value.trim();
  if (!question){
    return;
  };

  appendMessage("user", question)
  showLoading("Antwort wird generiert...");

  try {
    const payload = {
      question: question,
      mode: "STANDARD"
    };
    console.log("POST /api/chat", payload);
    const response = await fetch("/api/chat", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    console.log(`Status ${response.status}`);
    if (response.ok) {
      const data = await response.json();
      console.log("Data", data);
      appendAnswer(data);
    } else{
      const body = await response.body();
      throw new Error(body);
    }
  } catch (err) {
    showError(err.message);
  } finally {
    hideLoading();
  }
}

async function startCrawl() {
  const namespaceUrl = document.getElementById("namespaceUrl").value;
  const regexFilter = document.getElementById("regexFilter").value;
  const overwriteExisting = document.getElementById("overwrite").checked;

  showLoading("Crawler läuft...");

  try {
    const payload = {
        namespaceUrl,
        regexFilter,
        overwriteExisting
    };
    console.log("POST /api/crawl/wiki", payload);
    const response = await fetch("/api/crawl/wiki", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    console.log(`Status ${response.status}`);
    if (!response.ok) {
      const body = await response.body();
      throw new Error(body);
    }

    showStatus("Crawler erfolgreich abgeschlossen.");
  } catch (err) {
    showError(err.message);
  } finally {
    hideLoading();
  }
}

function appendAnswer(answer) {
  const chatLog = document.getElementById("chat-log");

  const wrapper = document.createElement("div");
  wrapper.className = "chat-answer";

  const textNode = document.createElement("div");
  textNode.className = "answer-text";

  const processedText = replaceDocIdsWithFootnotes(
    answer.text,
    answer.sources
  );

  textNode.textContent = processedText;
  wrapper.appendChild(textNode);

  const footnotes = buildFootnotes(answer.sources);
  if (footnotes) {
    wrapper.appendChild(footnotes);
  }

  chatLog.appendChild(wrapper);
}

function appendMessage(role, text) {
    const log = document.getElementById("chat-log");
    const div = document.createElement("div");
    div.className = `chat-message ${role}`;
    div.textContent = text;
    log.appendChild(div);
}

function buildFootnotes(sources) {
  const container = document.createElement("div");
  container.className = "answer-sources";

  const title = document.createElement("div");
  title.className = "sources-title";
  title.textContent = "Quellen:";
  container.appendChild(title);

  const list = document.createElement("ol");
  list.className = "sources-list";

  sources.forEach((source, index) => {
    const li = document.createElement("li");

    const link = document.createElement("a");
    link.href = source.source;
    link.target = "_blank";
    link.rel = "noopener noreferrer";

    link.textContent = source.source;
    link.title = `Referenziert durch: ${[...source.docIds].join(", ")}`;

    li.appendChild(link);
    list.appendChild(li);
  });

  container.appendChild(list);
  return container;
}

function replaceDocIdsWithFootnotes(text, sources) {
  let result = text;
  sources.forEach((source, index) => {
    const footnoteNumber = index + 1;

    source.docIds.forEach(docId => {
      const regex = new RegExp(`\\[${docId}\\]`, "g");
      result = result.replace(regex, `[${footnoteNumber}]`);
    });
  });
  return result;
}

function showLoading(text = "Bitte warten...") {
  const el = document.getElementById("loading");
  document.getElementById("loading-text").textContent = text;
  el.classList.remove("hidden");
}

function hideLoading() {
  document.getElementById("loading").classList.add("hidden");
}

function showError(message) {
  console.error(message);
  alert(message);
}

function showStatus(message) {
  console.log(message);
}
