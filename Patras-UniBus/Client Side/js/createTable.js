 const ip = "localhost";
let div = document.getElementById("tableDiv");
let select = document.getElementById("selectLine");
select.addEventListener("change", createTable);
createTableFirst();

function createTableFirst() {
    let line = localStorage.getItem("line");
    select.value = (line == 'line1' ? '1' : '2');
    createTable();
}

function createTable() {
    let id = parseInt(select.value);
    let table = document.createElement("table");
    table.setAttribute("class", "table border-warning");
    div.appendChild(table);
    let head = '<thead class="thead-light">' +
        '<tr>' +
        '<th scope="col">Λεωφορείο</th>' +
        '<th scope="col">Γραμμή</th>' +
        '<th scope="col">Επιβάτες</th>' +
        '<th scope="col">Επόμενη Στάση</th>' +
        '<th scope="col">Στοπ στην επόμενη στάση</th>' +
        '</tr>' +
        '</thead>';
    table.innerHTML = head;
    let body = document.createElement("tbody");
    table.appendChild(body);
    fetch("http://" + ip + ":8080//CampusBuses/buses/line?line=" + id).then(
        (response) => response.json().then(
            (BusesJson) => {
                console.log(BusesJson);
                for (let bus of BusesJson) {
                    let row = document.createElement("tr");
                    row.setAttribute("class", "table-secondary");
                    if (bus.passengers == 100) {
                        row.setAttribute("class", "table-warning");
                    }
                    if (!bus.location) {
                        row.setAttribute("class", "table-danger");
                    }
                    row.innerHTML = '<th scope="row" class="text-left">' + bus.id + '</th>' +
                        '<td class="text-left">' + bus.line + '</td>' +
                        '<td class="text-left">' + (bus.passengers ? bus.passengers : 0) + '</td>' +
                        '<td class="text-left">' + bus.nextStop + '</td>' +
                        '<td class="text-left">' + (bus.makeStop == false ? 'Όχι' : 'Ναι') + '</td>';
                    body.appendChild(row);
                }
                let update = setInterval(function () {
                    updateTable(body, id);
                }, 2000);
                select.addEventListener("change", function () {
                    localStorage.setItem("line", (select.value == 1 ? 'line1' : 'line2'));
                    clearInterval(update);
                    table.innerHTML = '';
                    table.remove();
                });
            })
    )
}



function updateTable(body, id) {
    fetch("http://" + ip + ":8080//CampusBuses/buses/line?line=" + id).then(
        (response) => response.json().then(
            (BusesJson) => {
                console.log(BusesJson);
                for (let i = 0; i < BusesJson.length; i++) {
                    let bus = BusesJson[i];
                    let row = body.childNodes[i];
                    if (bus.passengers && row.childNodes[2].innerText != bus.passengers) {
                        row.childNodes[2].innerText = bus.passengers;
                    }
                    if (row.childNodes[3].innerText != bus.nextStop) {
                        row.childNodes[3].innerText = bus.nextStop;
                    }
                    if (row.childNodes[4].innerText != bus.makeStop) {
                        row.childNodes[4].innerText = (bus.makeStop == false ? 'Όχι' : 'Ναι')
                    }
                    if (bus.passengers == 100) {
                        row.setAttribute("class", "table-warning");
                    }
                    else if (!bus.location) {
                        row.setAttribute("class", "table-danger");
                    }
                    else {
                        row.setAttribute("class", "table-secondary");
                    }
                }
            })
    )
}