interface Person {
    firstName: string;
    lastName: string;
}

function greeter(person: Person) {
    return "Ciao " + person.firstName + " " + person.lastName;
}

let user = { firstName: "Lippo", lastName: "Lippi"};


document.body.textContent = greeter(user);
