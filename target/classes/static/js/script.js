// script.js
const toggleSidebar = () => {
  const sidebar = document.querySelector(".sidebar");
  const content = document.querySelector(".content");
  if (sidebar.style.display === "none") {
    sidebar.style.display = "block";
    content.style.marginLeft = "20%";
  } else {
    sidebar.style.display = "none";
    content.style.marginLeft = "0%";
  }
};

// validation.js

function validateForm() {
  var email = document.getElementById("email").value;
  var phone = document.getElementById("phone").value;
  var emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  var phonePattern = /^\d{10}$/; // Change this according to your phone number format

  if (!emailPattern.test(email)) {
    alert("Please enter a valid email address.");
    return false;
  }

  return true;
}
//seach
const search = () => {
  // console.log("search");
  let query = $("#search-input").val();
  //
  if (query == "") {
    $(".search-result").hide();
  } else {
    console.log(query);

    //sending req to server
    let url = `http://localhost:8282/search/${query}`;

    fetch(url)
      .then((response) => {
        return response.json();
      })
      .then((data) => {
        console.log(data);
        let text = `<div class='list-group'>`;
        data.forEach((contact) => {
          text += `<a href='/normal/${contact.cId}/contact' class='list-group-item list-group-item-action' > ${contact.name}</a>`;
        });

        text += `</div>`;
        $(".search-result").html(text);
        $(".search-result").show();
      });
  }
};
