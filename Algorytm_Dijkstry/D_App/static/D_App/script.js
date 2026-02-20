const canvas = document.getElementById("graphCanvas");
    const ctx = canvas.getContext("2d");
    const results = document.getElementById("results");

    function resizeCanvas() {
      canvas.width = canvas.clientWidth;
      canvas.height = canvas.clientHeight;
    }
    window.addEventListener("resize", resizeCanvas);
    resizeCanvas();

    let mode = null;
    let nodeId = 0;
    const graphNodes = [];
    const graphEdges = [];
    let selectedNode = null;
    let lastDijkstraResult = null;
    let highlightStartNode = null;

    document.getElementById("add-node").addEventListener("click", () => {mode="node"; console.log("Dodaj węzeł");});
    document.getElementById("add-edge").addEventListener("click", () => {mode="edge"; console.log("Dodaj krawędź");});
    document.getElementById("generate-graph").addEventListener("click", async ()=> {
        await generateRandomGraph();
        console.log("Wygenerowano Graf")
    });
    document.getElementById("run-dijkstra").addEventListener("click", async () => {
        results.innerHTML = "⏳ Liczę najkrótsze ścieżki";
        await runDijkstra();
        console.log("Uruchamiam Dijkstrę");
    });
    document.getElementById("highlight-mode").addEventListener("click", () => {mode="highlight"; console.log("Wybierz węzeł docelowy")
        alert("Kliknij w węzeł docelowy, aby podświetlić najkrótszą ścieżkę.");
    });
//-------------Funkcje canvasa
    canvas.addEventListener("click", (e) => {
        const rect = canvas.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const y = e.clientY - rect.top;
        const clickedNode=getNodeAt(x, y);
        //Tryb węzłów
        if (mode === "node") {
            if (!clickedNode) {
                addNode(x, y);
        }
        return;
    }
        // Tryb krawędzi
        if (mode === "edge") {
            if (clickedNode) {
                handleEdgeClick(x, y);
        }
        return;
    }
        // Tryb podświetlania
        if (mode === "highlight") {
            if (!highlightStartNode) {
                alert("Najpierw uruchom Dijkstrę, aby ustawić węzeł startowy.");
                return;
            }
            if (!clickedNode) {
                return;
        }
        highlightPath(clickedNode.id);
    }
    });
        function getNodeAt(x, y) {
        return graphNodes.find(n => {
            const dx = n.x - x;
            const dy = n.y - y;
            return Math.sqrt(dx*dx + dy*dy) <= 15;
    });
    }
        function addNode(x, y) {
        const newNode = {
            id: "N" + nodeId++,
            x, y
    };
    graphNodes.push(newNode);
    drawGraph();
}
function handleEdgeClick(x, y) {
    const node = getNodeAtPosition(x, y);
    if (!node) return;
    if (!selectedNode) {
        selectedNode = node;
        return;
    }
    if (selectedNode.id !== node.id) {
        let weight;
        while (true) {
            weight = prompt("Podaj wagę krawędzi (liczba dodatnia):");
            if (weight === null) {
                selectedNode = null;
                return;
            }
            weight = parseInt(weight);
            if (!isNaN(weight) && weight >= 0) {
                break;
            }
            alert("Nieprawidłowa waga! Podaj liczbę dodatnią.");
        }
        graphEdges.push({
            u: selectedNode.id,
            v: node.id,
            weight: weight
        });
        drawGraph();
    }
    selectedNode = null;
}
    function getNodeAtPosition(x, y) {
    return graphNodes.find(n =>
        Math.hypot(n.x - x, n.y - y) < 20
    );
}
function drawGraph(highlightEdges=[]) {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    //krawędzie
    graphEdges.forEach(edge => {
        const A = graphNodes.find(n => n.id === edge.u);
        const B = graphNodes.find(n => n.id === edge.v);
        const highlight = highlightEdges.some(h =>
        (h.u === edge.u && h.v === edge.v) || (h.u === edge.v && h.v === edge.u));
        ctx.strokeStyle = highlight ? "red" : "black";
        ctx.lineWidth = highlight ? 4 : 2;
        ctx.beginPath();
        ctx.moveTo(A.x, A.y);
        ctx.lineTo(B.x, B.y);
        ctx.stroke();
        // waga
        const midX = (A.x + B.x) / 2;
        const midY = (A.y + B.y) / 2;
        ctx.fillStyle = "black";
        ctx.fillText(edge.weight, midX, midY);
    });
    //węzły
    graphNodes.forEach(node => {
        ctx.beginPath();
        ctx.arc(node.x, node.y, 15, 0, Math.PI * 2);
        ctx.fillStyle = "#05a8fa";
        ctx.fill();
        ctx.stroke();
        // ID węzła
        ctx.fillStyle = "#000";
        ctx.fillText(node.id, node.x - 5, node.y + 5);
    });
}
function highlightPath(targetId) {
    if (!lastDijkstraResult || !highlightStartNode) {
        alert("Najpierw uruchom algorytm Dijkstry!");
        return;
    }
    const startId = highlightStartNode.id;
    const { prev } = lastDijkstraResult;
    // Odtwarzanie ścieżki
    let path = [];
    let current = targetId;
    while (current !== startId) {
        const previous = prev[current];
        if (!previous) break; // brak ścieżki
        path.push({ u: previous, v: current });
        current = previous;
    }
    if (path.length === 0) {
        alert("Brak ścieżki między węzłami!");
        return;
    }
    drawGraph(path.reverse());
}
//--------------funkcje backendu
async function generateRandomGraph() {
    const n = parseInt(prompt("Podaj liczbę wierzchołków wierzchołków?", "5"));
    if (!n || n <= 0) return;
    try {
        const res = await fetch("/generate_graph/", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "X-CSRFToken": getCookie("csrftoken")
            },
            body: JSON.stringify({ n })
        });
        if (!res.ok) {
            throw new Error("Błąd w odpowiedzi serwera");
        }
        const data = await res.json();
        graphNodes.length = 0;
        graphEdges.length = 0;
        data.nodes.forEach(node => graphNodes.push(node));
        data.edges.forEach(edge => graphEdges.push(edge));
        drawGraph();
        console.log("Losowy graf wygenerowany:", data);

    } catch (err) {
        console.error("Nie udało się wygenerować grafu:", err);
    }
}
async function runDijkstra() {
    const nodes = graphNodes.map(n => n.id);
    const edges = graphEdges.map(e => [e.u, e.v, e.weight]);
    const startNode = prompt("Podaj wierzchołek startowy:", nodes[0]);
    if (!startNode || !nodes.includes(startNode)) {
        alert("Nieprawidłowy wierzchołek startowy!");
        return;
    }
    const res = await fetch("/run_dijkstra/", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-CSRFToken": getCookie("csrftoken")
        },
        body: JSON.stringify({
            graph: {nodes, edges},
            start: startNode
        })
    });
    const data = await res.json();
    console.log("Odpowiedź Django:", data);
    console.log("Wyniki: ", data);
    if(data.distances && data.previous){
        Display(data.distances, data.previous, startNode);
        lastDijkstraResult = {
            distances: data.distances,
            prev: data.previous
        };
    highlightStartNode = startNode;
    }
}
function Display(distances, previous, start) {
    const panel = document.getElementById("results");
    panel.innerHTML = `<strong>Najkrótsze ścieżki z ${start}:</strong><br><br>`;
    for (const [node, distance] of Object.entries(distances)) {
        const path = reconstructPath(node, previous, start);
        panel.innerHTML += `${start}<br>`;
        for (let i = 1; i < path.length; i++) {
            const prev = path[i - 1];
            const curr = path[i];
            const w = getWeight(prev, curr);
            panel.innerHTML += `-${w}-> ${curr} `;
        }
        panel.innerHTML += `<br><br>`;
    }
}
function getWeight(u, v) {
    const edge = graphEdges.find(e =>
        (e.u === u && e.v === v) ||
        (e.u === v && e.v === u)
    );
    return edge ? edge.weight : "?";
}
function reconstructPath(node, previous, start) {
    let path = [];
    let current = node;
    while (current !== null) {
        path.push(current);
        current = previous[current];
    }
    path.reverse();
    return path;
}
//Token
function getCookie(name) {
    let cookieValue = null;
    if (document.cookie && document.cookie !== "") {
        const cookies = document.cookie.split(";");
        for (let i = 0; i < cookies.length; i++) {
            const cookie = cookies[i].trim();
            if (cookie.substring(0, name.length + 1) === (name + "=")) {
                cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                break;
            }
        }
    }
    return cookieValue;
}