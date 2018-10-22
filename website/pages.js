var input = document.getElementById("upload");
var uploadButton = document.getElementById("uploadbutton");
var imagePreview = document.getElementById("preview");

input.onchange = function() {
	var ext = this.files[0].name.split(".")[1].toLowerCase();
	var size = (this.files[0].size / 1000).toFixed(1);
	if(ext == "jpg" || ext == "png")
	{
		if(size <= 250)
		{
			var gallery = document.getElementById("preview");
			var thumb = document.createElement("div");
			var img = document.createElement("img");
			var reader = new FileReader();
			
			thumb.classList.add("thumbnail");
			img.file = this.files[0];
			thumb.appendChild(img);
			gallery.appendChild(thumb);

			reader.onload = (function() 
			{ 
				return function(e) 
				{ 
					imagePreview.src = e.target.result; 
				}; 
			})(img);
			reader.readAsDataURL(this.files[0]);
			uploadButton.value = "Change Image";
		}
		else
		{
			alert("File Upload Error: File size must be less than 250KB!\n\nThe size of the selected file is " + size + "KB.");
			this.value = null;
		}
	}
	else 
	{
		alert("File Upload Error: File type must be JPG or PNG!\n\nThe type of the selected file is " + ext.toUpperCase() + ".");
		this.value = null;
	}	
	
};