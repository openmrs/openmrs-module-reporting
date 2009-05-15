/* Arabic Translation for jQuery UI date picker plugin. */
/* Khaled Al Horani -- koko.dw@gmail.com */
jQuery(function($){
	$.datepicker.regional['ar'] = {
		clearText: '����',
		clearStatus: '���� ������� ������',
		closeText: '�����',
		closeStatus: '����� ���� ���',
		prevText: '<������',
		prevStatus: '��� ����� ������',
		nextText: '������>',
		nextStatus: '��� ����� ������',
		currentText: '�����',
		currentStatus: '��� ����� ������',
		monthNames: ['����� ������', '����', '����', '�����', '����', '������', '����', '��', '�����',	'����� �����', '����� ������', '����� �����'],
		monthNamesShort: ['����� ������', '����', '����', '�����', '����', '������', '����', '��', '�����',	'����� �����', '����� ������', '����� �����'],
		monthStatus: '��� ��� ���',
		yearStatus: '��� ��� ����',
		weekHeader: '�����',
		weekStatus: '����� �����',
		dayNames: ['�����', '�����', '�������', '��������', '��������', '������', '������'],
		dayNamesShort: ['�����', '�����', '�������', '��������', '��������', '������', '������'],
		dayNamesMin: ['�����', '�����', '�������', '��������', '��������', '������', '������'],
		dayStatus: '���� DD ����� ����� �� �������',
		dateStatus: '���� D, M d',
		dateFormat: 'dd/mm/yy', firstDay: 0, 
		initStatus: '���� ���',
		isRTL: true
	};
	$.datepicker.setDefaults($.datepicker.regional['ar']);
});