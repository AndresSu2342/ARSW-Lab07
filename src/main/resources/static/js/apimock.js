//@author hcadavid

apimock=(function(){

	var mockdata=[];

	mockdata["johnconnor"] = [
            { author: "johnconnor", points: [{ x: 150, y: 120 }, { x: 215, y: 115 }], name: "house" },
            { author: "johnconnor", points: [{ x: 340, y: 240 }, { x: 15, y: 215 }], name: "gear" },
            { author: "johnconnor", points: [{ x: 145, y: 145 }, { x: 111, y: 111 }], name: "fabric" },
            { author: "johnconnor", points: [{ x: 141, y: 141 }, { x: 69, y: 69 }], name: "building" }
        ];

    mockdata["maryweyland"] = [
            { author: "maryweyland", points: [{ x: 140, y: 140 }, { x: 115, y: 115 }], name: "house2" },
            { author: "maryweyland", points: [{ x: 140, y: 140 }, { x: 115, y: 115 }], name: "gear2" },
            { author: "maryweyland", points: [{ x: 160, y: 160 }, { x: 80, y: 80 }], name: "fabric2" },
            { author: "maryweyland", points: [{ x: 100, y: 100 }, { x: 185, y: 185 }], name: "building2" }
        ];


	return {
		getBlueprintsByAuthor:function(authname,callback){
			callback(
				mockdata[authname]
			);
		},

		getBlueprintsByNameAndAuthor:function(authname,bpname,callback){

			callback(
				mockdata[authname].find(function(e){return e.name===bpname})
			);
		}
	}	

})();