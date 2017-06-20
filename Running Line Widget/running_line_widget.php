<?php

/* 

Plugin Name: Running Line Widget

Version: 1.0

Author: Valdas email: valdasns@gmail.com

Description: Running Line Widget 2016

*/ 

class running_line extends WP_Widget{
	public function __construct(){
		parent::__construct('running_line',$name =__('Running Line'));
	}
	public function widget($args, $instance){
		
		echo '<div class="running-line"><marquee scrolldelay="60" scrollamount="3" bgcolor="#E4EEF0" onmouseover="this.stop();" onmouseout="this.start();">';
		echo $instance['title'];
		echo '</marquee>  </div>';
		}

	
	public function form($instance){
		$title='bana';
		$posts_per_page = 3;
		
		if (isset($instance['title'])){
			$title= $instance['title'];
		}
		echo '<p>';
		echo '<label for="'.$this->get_field_id('title').'">Enter running line text: </label>';
		echo '<textarea rows="4" cols="50" type="text" name="'.$this->get_field_name('title').'" id="'.$this->get_field_id('title').'">'.$title.'</textarea>';
		echo '</p>';
	}
	public function update($new_instance, $old_instance){
		$instance['title'] = $new_instance['title']; 
		return $instance;
	}
}

//add stylesheet
function register_plugin_styles_running_line() {
	wp_register_style( 'header-widget', plugins_url( 'header-widget/running_line.css' ) );
	wp_enqueue_style( 'header-widget' );
}

add_action( 'wp_enqueue_scripts', 'register_plugin_styles_running_line' );

add_action('widgets_init',function(){
	register_widget('running_line');
});

?>